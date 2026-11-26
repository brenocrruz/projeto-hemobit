param(
    [Parameter(Mandatory)][uri]$Url,
    [Parameter(Mandatory)][pscredential]$Credencial
)

$ErrorActionPreference = 'Stop'
if ($Url.Scheme -ne 'https') { throw 'Use a URL HTTPS remota do Grafana.' }
$base = $Url.AbsoluteUri.TrimEnd('/')
$usuario = $Credencial.UserName
$senha = $Credencial.GetNetworkCredential().Password
$token = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes("${usuario}:${senha}"))
$headers = @{ Authorization = "Basic $token" }
$org = Invoke-RestMethod "$base/api/user/orgs" -Headers $headers -MaximumRedirection 0
if (@($org).Count -ne 1 -or $org[0].role -ne 'Viewer') { throw 'Papel Viewer nao confirmado.' }
foreach ($uid in 'hemobit-aplicacao', 'hemobit-infra') {
    $null = Invoke-RestMethod "$base/api/dashboards/uid/$uid" -Headers $headers -MaximumRedirection 0
}
$estado = Invoke-RestMethod "$base/api/datasources/proxy/uid/prometheus/api/v1/query?query=up" -Headers $headers -MaximumRedirection 0
$targets = @($estado.data.result)
if ($targets.Count -ne 4 -or @($targets | Where-Object { $_.value[1] -ne '1' }).Count) {
    throw 'Os quatro targets UP nao foram confirmados.'
}
Write-Output "$([DateTimeOffset]::Now.ToString('o')) - Viewer autenticado, dois dashboards acessiveis e quatro targets UP."
