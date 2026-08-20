@echo off
setlocal

echo Building CodeTriX execution images...

echo Building C image...
docker build -t codetrix-c ./c
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo Building C++ image...
docker build -t codetrix-cpp ./cpp
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo Building Java image...
docker build -t codetrix-java ./java
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo Building Python image...
docker build -t codetrix-python ./python
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo.
echo All images built successfully!
echo.
docker images | findstr codetrix

endlocal
