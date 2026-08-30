$ErrorActionPreference = 'Stop'

$projectRoot = (Get-Location).Path
$planPath = Join-Path $projectRoot 'test/ui-test-plan.md'
$buildPath = Join-Path $projectRoot '.test-ui-build'
$sourceFiles = @(Get-ChildItem (Join-Path $projectRoot 'src/main/java') -Recurse -Filter '*.java' | Select-Object -ExpandProperty FullName)

if (-not (Test-Path $planPath)) {
    throw "Test plan not found: $planPath"
}

New-Item -ItemType Directory -Force -Path $buildPath | Out-Null
javac -d $buildPath $sourceFiles

$markdown = Get-Content -Raw -LiteralPath $planPath
$caseMatches = [regex]::Matches($markdown, '(?ms)^## Test case .*?(?=^## Test case |\z)')

if ($caseMatches.Count -eq 0) {
    throw 'No test cases found in test/ui-test-plan.md'
}

function Get-CodeBlock {
    param([string] $section, [string] $heading)
    $fence = (([char]96).ToString() * 3)
    $pattern = '(?ms)^### ' + [regex]::Escape($heading) + '\s*' + $fence + '(?:text)?\s*(.*?)\s*' + $fence
    $match = [regex]::Match($section, $pattern)
    if (-not $match.Success) {
        throw ('Missing ' + $heading + ' code block in test case')
    }
    return $match.Groups[1].Value.Trim()
}

function Normalize-Lines([string] $value) {
    $lines = $value -split '\r?\n'
    return @($lines | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne '' })
}

$caseNumber = 0
foreach ($caseMatch in $caseMatches) {
    $caseNumber++
    $section = $caseMatch.Value
    $title = ([regex]::Match($section, '(?m)^## (.*)$')).Groups[1].Value
    $inputText = Get-CodeBlock $section 'Input'
    $expectedText = Get-CodeBlock $section 'Expected output'

    Write-Output "===== Test ${caseNumber}: $title ====="
    Write-Output 'Console input:'
    Write-Output $inputText

    $psi = [System.Diagnostics.ProcessStartInfo]::new()
    $psi.FileName = 'java'
    $psi.Arguments = "-cp `"$buildPath`" epi.Epi"
    $psi.WorkingDirectory = $projectRoot
    $psi.RedirectStandardInput = $true
    $psi.RedirectStandardOutput = $true
    $psi.RedirectStandardError = $true
    $psi.UseShellExecute = $false
    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $psi
    [void]$process.Start()
    $process.StandardInput.WriteLine($inputText.Replace("`n", "`r`n"))
    $process.StandardInput.Close()
    $actualText = $process.StandardOutput.ReadToEnd()
    $errorText = $process.StandardError.ReadToEnd()
    $process.WaitForExit()

    Write-Output 'Console output:'
    Write-Output $actualText.TrimEnd()

    $actualLines = Normalize-Lines $actualText
    $expectedLines = Normalize-Lines $expectedText
    $position = 0
    $failedLine = $null
    foreach ($expectedLine in $expectedLines) {
        $found = $false
        while ($position -lt $actualLines.Count) {
            if ($actualLines[$position] -eq $expectedLine) {
                $found = $true
                $position++
                break
            }
            $position++
        }
        if (-not $found) {
            $failedLine = $expectedLine
            break
        }
    }

    if ($process.ExitCode -ne 0 -or $failedLine) {
        Write-Output 'FAILED'
        Write-Output 'Expected output:'
        Write-Output $expectedText
        if ($failedLine) { Write-Output "Missing expected line: $failedLine" }
        if ($errorText) { Write-Output "Process error: $errorText" }
        exit 1
    }

    Write-Output 'PASSED'
}

Write-Output "All $caseNumber UI tests passed."
