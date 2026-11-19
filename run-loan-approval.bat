@echo off
setlocal
powershell -ExecutionPolicy Bypass -File "%~dp0run-loan-approval.ps1" %*
