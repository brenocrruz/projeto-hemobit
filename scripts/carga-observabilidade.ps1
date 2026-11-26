param(
    [uri]$BaseUrl = 'http://localhost:8080',
    [ValidateRange(1, 86400)][int]$Segundos = 180
)

$ErrorActionPreference = 'Stop'
$inicio = [DateTimeOffset]::Now
$fim = $inicio.AddSeconds($Segundos)
$sucessos = 0
$errosEsperados = 0
Write-Output "Inicio: $($inicio.ToString('o'))"
while ([DateTimeOffset]::Now -lt $fim) {
    $codigo = curl.exe -sS -o NUL -w '%{http_code}' --max-time 8 "$($BaseUrl.AbsoluteUri.TrimEnd('/'))/api/indicadores/estoque-real"
    if ($LASTEXITCODE -ne 0 -or $codigo -ne '200') { throw "Consulta de estoque retornou $codigo" }
    $sucessos++

    if ($sucessos % 10 -eq 1) {
        $codigo = curl.exe -sS -o NUL -w '%{http_code}' --max-time 8 "$($BaseUrl.AbsoluteUri.TrimEnd('/'))/api/indicadores/rota-inexistente"
        if ($LASTEXITCODE -ne 0 -or $codigo -ne '404') { throw "Caso de erro retornou $codigo" }
        $errosEsperados++
    }

    Start-Sleep -Milliseconds 750
}
Write-Output "Fim: $([DateTimeOffset]::Now.ToString('o'))"
Write-Output "HTTP 200 no estoque: $sucessos"
Write-Output "HTTP 404 na rota inexistente: $errosEsperados"
