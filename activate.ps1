# Activates JDK 17 for the current PowerShell session only.
# Usage: . .\activate.ps1     (note the leading dot + space — "dot-source")
#
# Affects only this shell. Close the window to revert.

$searchRoots = @(
    "C:\Program Files\Eclipse Adoptium",
    "C:\Program Files\Java",
    "C:\Program Files\Microsoft",
    "C:\Program Files\Zulu",
    "C:\Program Files\BellSoft\LibericaJDK-17"
)

$jdk17 = $null
foreach ($root in $searchRoots) {
    if (Test-Path $root) {
        $candidate = Get-ChildItem -Path $root -Directory -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -match "jdk-?17" } |
            Sort-Object Name -Descending |
            Select-Object -First 1
        if ($candidate) {
            $jdk17 = $candidate.FullName
            break
        }
    }
}

if (-not $jdk17) {
    Write-Host "JDK 17 not found in standard locations." -ForegroundColor Red
    Write-Host "Install from: https://adoptium.net/temurin/releases/?version=17"
    Write-Host "Or set manually: `$env:JAVA_HOME = 'C:\path\to\jdk-17'"
    return
}

$env:JAVA_HOME = $jdk17
$env:PATH = "$jdk17\bin;$env:PATH"

Write-Host "JDK 17 activated for this shell" -ForegroundColor Green
Write-Host "  JAVA_HOME = $jdk17"
Write-Host ""
java -version
