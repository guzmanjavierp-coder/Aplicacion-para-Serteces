$ErrorActionPreference = 'Stop'
$proyecto = Split-Path -Parent $PSScriptRoot
$salida = Join-Path $proyecto 'build/tercera-entrega'
New-Item -ItemType Directory -Force $salida | Out-Null
$fuentes = @(Get-ChildItem (Join-Path $proyecto 'src') -Recurse -Filter '*.java' | ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' })
$fuentes += @(Get-ChildItem $PSScriptRoot -Filter '*.java' | ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' })
$compilacion = '-encoding UTF-8 --release 17 -d "' + $salida.Replace('\', '/') + '"' + [Environment]::NewLine + ($fuentes -join [Environment]::NewLine)
$argumentos = Join-Path $salida 'javac.args'
[IO.File]::WriteAllText($argumentos, $compilacion, (New-Object System.Text.UTF8Encoding($false)))
& javac ('@' + $argumentos)
if ($LASTEXITCODE -ne 0) { throw 'Fallo la compilacion.' }
foreach ($clasePrueba in @('TerceraEntregaTest', 'AjustesTest', 'ReporteServiciosTest')) {
$datosPrueba = Join-Path $proyecto ('build/prueba-' + $clasePrueba + '-' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory $datosPrueba | Out-Null
New-Item -ItemType File (Join-Path $datosPrueba '.pruebas-aisladas') | Out-Null
$ejecucion = Join-Path $datosPrueba 'java.args'
[IO.File]::WriteAllText($ejecucion, ('-Dproyecto.pruebas.fixtures="' + (Join-Path $PSScriptRoot 'fixtures').Replace('\', '/') + '" -cp "' + $salida.Replace('\', '/') + '" ' + $clasePrueba), (New-Object System.Text.UTF8Encoding($false)))
Push-Location -LiteralPath $datosPrueba
try {
    & java ('@' + $ejecucion)
    if ($LASTEXITCODE -ne 0) { throw 'Fallaron las pruebas.' }
} finally { Pop-Location }
}
