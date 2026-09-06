<#
    Captures a real screenshot of a single window by its title.

    Used to take genuine screen captures of the program running in a console window,
    rather than rendering the text into an image. Only the named window is captured, so
    nothing else on the desktop ends up in the picture.

    Usage:
        powershell -ExecutionPolicy Bypass -File docs/capture-window.ps1 -Title "HospitalDemo" -Out docs/screenshots-real/01.png
#>
param(
    [Parameter(Mandatory = $true)][string]$Title,
    [Parameter(Mandatory = $true)][string]$Out,
    # Pixels trimmed from every edge. Even the DWM frame bounds can leave a hairline of
    # whatever sits behind the window, so a couple of pixels are dropped to be sure the
    # capture contains nothing but the console.
    [int]$Inset = 3
)

Add-Type -AssemblyName System.Drawing

Add-Type @"
using System;
using System.Text;
using System.Collections.Generic;
using System.Runtime.InteropServices;
public class Win {
    public delegate bool EnumProc(IntPtr h, IntPtr l);
    [DllImport("user32.dll")] public static extern bool EnumWindows(EnumProc cb, IntPtr l);
    [DllImport("user32.dll", CharSet = CharSet.Unicode)] public static extern int GetWindowText(IntPtr h, StringBuilder s, int c);
    [DllImport("user32.dll")] public static extern bool IsWindowVisible(IntPtr h);

    // Windows Terminal hosts every console window inside a single process, so the
    // process MainWindowHandle points at whichever window it opened first. Enumerating
    // the top level windows and matching the title is the only way to reach the right one.
    public static List<IntPtr> FindByTitle(string title) {
        List<IntPtr> found = new List<IntPtr>();
        EnumWindows(delegate(IntPtr h, IntPtr l) {
            if (!IsWindowVisible(h)) return true;
            StringBuilder sb = new StringBuilder(512);
            GetWindowText(h, sb, 512);
            if (sb.ToString() == title) found.Add(h);
            return true;
        }, IntPtr.Zero);
        return found;
    }
    [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr h);
    [DllImport("user32.dll")] public static extern bool GetWindowRect(IntPtr h, out RECT r);
    [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr h, int cmd);
    [DllImport("user32.dll")] public static extern bool SetWindowPos(IntPtr h, IntPtr after, int x, int y, int cx, int cy, uint flags);
    [DllImport("user32.dll")] public static extern IntPtr GetForegroundWindow();
    [DllImport("dwmapi.dll")] public static extern int DwmGetWindowAttribute(IntPtr h, int a, out RECT r, int s);
    [StructLayout(LayoutKind.Sequential)] public struct RECT { public int Left, Top, Right, Bottom; }
}
"@

$matches = [Win]::FindByTitle($Title)
if ($matches.Count -eq 0) {
    Write-Output "WINDOW NOT FOUND: $Title"
    exit 1
}
if ($matches.Count -gt 1) {
    # Two windows carrying the same title means a previous run has not closed yet, and
    # there is no way to tell which is which - better to fail than photograph the wrong one.
    Write-Output "ABORTED: $($matches.Count) windows are titled '$Title'"
    exit 3
}
$handle = $matches[0]

# The capture reads pixels from the screen, so the window must genuinely be on top -
# otherwise whatever is covering it gets photographed instead. SetForegroundWindow alone
# is unreliable when called from a background process, so the window is also pinned
# topmost, which Windows does allow.
#
# The window is also moved to the top left corner, because notification toasts appear in
# the bottom right of the screen and float above even a topmost window - parking the
# console away from that corner keeps them out of the picture.
[Win]::ShowWindow($handle, 9) | Out-Null                                  # 9 = SW_RESTORE
[Win]::SetWindowPos($handle, [IntPtr](-1), 0, 0, 0, 0, 0x0041) | Out-Null # HWND_TOPMOST, keep size, move to 0,0
[Win]::SetForegroundWindow($handle) | Out-Null
Start-Sleep -Milliseconds 1200

# Refuse to save anything unless this window really is the one in front.
if ([Win]::GetForegroundWindow() -ne $handle) {
    [Win]::SetWindowPos($handle, [IntPtr](-2), 0, 0, 0, 0, 0x0043) | Out-Null
    Write-Output "ABORTED: '$Title' is not the foreground window, refusing to capture what is covering it"
    exit 2
}

# GetWindowRect includes the invisible drop shadow border, which would capture a strip of
# whatever is behind the window. The DWM extended frame bounds give the real visible edges,
# so only the console window itself ends up in the image.
$rect = New-Object Win+RECT
$dwmOk = [Win]::DwmGetWindowAttribute($handle, 9, [ref]$rect, 16)   # 9 = EXTENDED_FRAME_BOUNDS
if ($dwmOk -ne 0) {
    [Win]::GetWindowRect($handle, [ref]$rect) | Out-Null
    Write-Output "  (falling back to GetWindowRect)"
}

$left   = $rect.Left + $Inset
$top    = $rect.Top + $Inset
$width  = ($rect.Right - $rect.Left) - (2 * $Inset)
$height = ($rect.Bottom - $rect.Top) - (2 * $Inset)
if ($width -le 0 -or $height -le 0) {
    Write-Output "WINDOW HAS NO SIZE"
    exit 1
}

$bitmap   = New-Object System.Drawing.Bitmap $width, $height
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$graphics.CopyFromScreen($left, $top, 0, 0, $bitmap.Size)

$folder = Split-Path -Parent $Out
if ($folder -and -not (Test-Path $folder)) { New-Item -ItemType Directory -Path $folder | Out-Null }

$bitmap.Save($Out, [System.Drawing.Imaging.ImageFormat]::Png)
$graphics.Dispose()
$bitmap.Dispose()

Write-Output "CAPTURED ${width}x${height} -> $Out"
