@ECHO off
ECHO exporting jars..

FOR %%n in (Backup, Bot, Core, Market, Masterbook, ModTools, Survival, Teleport, Claims) DO jar cfv %%n.jar -C E:\github\%%n\bin .
