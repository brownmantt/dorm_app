@echo off
REM UNC パス（\\server\share）上でも Vite を起動するためのラッパー
pushd "%~dp0"
set CHOKIDAR_USEPOLLING=true
call npm run dev
popd
