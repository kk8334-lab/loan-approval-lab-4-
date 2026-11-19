param(
    [switch]$SkipC8Run,
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$workerDir = Join-Path $projectRoot "worker"
$jarPath = Join-Path $workerDir "target\loan-approval-1.0.0.jar"

function Assert-Command {
    param([string]$Name, [string]$InstallHint)

    $cmd = Get-Command $Name -ErrorAction SilentlyContinue
    if (-not $cmd) {
        throw "Required command '$Name' was not found. $InstallHint"
    }
    return $cmd.Source
}

Write-Host "Project root: $projectRoot"

# Make sure Java is available.
$javaExe = Assert-Command -Name "java" -InstallHint "Install Java 17+ and ensure it is on PATH."

# Build the JAR if it does not already exist.
if (-not $SkipBuild -and -not (Test-Path $jarPath)) {
    $mvnExe = Assert-Command -Name "mvn" -InstallHint "Install Maven and add it to PATH, or run the project from an environment with Maven available."
    Write-Host "Building worker project with Maven..."
    Push-Location $workerDir
    try {
        & $mvnExe "clean" "package" "-q"
        if ($LASTEXITCODE -ne 0) {
            throw "Maven build failed."
        }
    }
    finally {
        Pop-Location
    }
}

if (-not (Test-Path $jarPath)) {
    throw "Build artifact was not found at '$jarPath'. Re-run without -SkipBuild after installing Maven."
}

if (-not $SkipC8Run) {
    $c8runExe = Get-Command "c8run" -ErrorAction SilentlyContinue
    if ($c8runExe) {
        Write-Host "Starting C8Run..."
        Start-Process -FilePath $c8runExe.Source -ArgumentList "start" -NoNewWindow
        Start-Sleep -Seconds 10
    }
    else {
        Write-Host "C8Run CLI was not found on PATH. Start Camunda 8 locally using C8Run manually, then continue."
        Write-Host "Expected ports: 26500 (Zeebe), 8080 (REST), and Tasklist/Operate UI on the C8Run default ports."
    }
}

Write-Host "Starting Spring Boot app on http://localhost:8081 ..."
$proc = Start-Process -FilePath $javaExe -ArgumentList @('-jar', $jarPath) -WorkingDirectory $workerDir -PassThru
Write-Host "App process started with PID $($proc.Id)"
Write-Host ""
Write-Host "Useful checks:"
Write-Host "  - API: http://localhost:8081/api/credit-assessment"
Write-Host "  - C8Run UI: http://localhost:8080"
Write-Host "  - Tasklist: http://localhost:8082"
Write-Host ""
Write-Host "When you are ready to stop the app, use: Stop-Process -Id $($proc.Id)"
