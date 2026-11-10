@echo off
chcp 65001 >nul
echo ============================================================
echo   KHOI DONG WEB DEMO SIEU THI - http://localhost:8088
echo ============================================================
cd /d "%~dp0"

set "MVN_CMD=mvn"
where mvn >nul 2>&1
if errorlevel 1 (
    for /d %%D in ("C:\Program Files\JetBrains\IntelliJ IDEA*") do (
        if exist "%%D\plugins\maven\lib\maven3\bin\mvn.cmd" (
            set "MVN_CMD=%%D\plugins\maven\lib\maven3\bin\mvn.cmd"
        )
    )
)

echo Dang chay voi Maven: "%MVN_CMD%"
"%MVN_CMD%" spring-boot:run
pause
