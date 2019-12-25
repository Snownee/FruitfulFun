@echo off

set modid=fruittrees

setlocal enabledelayedexpansion

for %%x in (%*) do (
    set file=leaves_%%x_3.json
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
        echo       "parent": "%modid%:block/fruit_md"
        echo     }
        echo   }
        echo }
    ) > !file!

)