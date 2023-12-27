@echo off

set modid=fruitfulfun

setlocal enabledelayedexpansion

for %%x in (%*) do (
    echo Making %%x_leaves.json
    (
        echo {
        echo   "variants": {
        echo     "age=0": { "model": "%modid%:block/leaves_%%x" },
        echo     "age=1": { "model": "%modid%:block/leaves_%%x" },
        echo     "age=2": { "model": "%modid%:block/leaves_%%x_2" },
        echo     "age=3": { "model": "%modid%:block/leaves_%%x_3" }
        echo   }
        echo }
    ) > %%x_leaves.json

)
