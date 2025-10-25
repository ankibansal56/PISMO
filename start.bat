@echo off
echo ============================================
echo Pismo Account Service - Quick Start
echo ============================================
echo.

echo Checking Java version...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21 or higher
    pause
    exit /b 1
)
echo.

echo Building the project...
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)
echo.

echo Starting the application...
echo The application will be available at: http://localhost:8080
echo Swagger UI will be available at: http://localhost:8080/swagger-ui.html
echo H2 Console will be available at: http://localhost:8080/h2-console
echo.
echo Press Ctrl+C to stop the application
echo.

call mvnw.cmd spring-boot:run
