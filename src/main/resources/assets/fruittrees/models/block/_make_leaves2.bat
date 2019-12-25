@echo off

set modid=fruittrees

setlocal enabledelayedexpansion

for %%x in (%*) do (
    set file=leaves_%%x_2.json
    echo Making !file!
    (
        echo {
        echo   "loader": "forge:composite",
        echo   "textures": {
        echo     "particle": "%modid%:block/leaves_%%x"
        echo   },
        echo   "parts": {
        echo     "0": {
        echo       "parent": "%modid%:block/leaves_%%x"
        echo     },
        echo     "1": {
        echo       "parent": "%modid%:block/flowers",
        echo       "textures": {
        echo         "0": "%modid%:block/flowers_%%x"
        echo       }
        echo     }
        echo   }
        echo }
    ) > !file!

)