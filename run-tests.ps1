$ErrorActionPreference = "Stop"

$envFile = Join-Path $PSScriptRoot ".env"

if (-not (Test-Path $envFile)) {
    throw ".env file not found."
}

Get-Content $envFile | ForEach-Object {
    if ($_ -match '^\s*([^#\s][^=]*)=(.*)$') {
        $name = $matches[1].Trim()
        $value = $matches[2].Trim()
        [Environment]::SetEnvironmentVariable($name, $value, "Process")
    }
}

& (Join-Path $PSScriptRoot "mvnw.cmd") test
exit $LASTEXITCODE
