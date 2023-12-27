@echo off

set modid=fruitfulfun

setlocal enabledelayedexpansion

for %%x in (%*) do (
    set file=leaves_%%x.json
    echo Making !file!
    (
        echo {
        echo   "parent": "block/leaves",
        echo   "textures": {
        echo     "all": "%modid%:block/leaves_%%x"
        echo   }
        echo }
    ) > !file!

)
