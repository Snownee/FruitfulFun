@echo off
:: Vazkii's JSON creator for items
:: Put in your /resources/assets/%modid%/models/item
:: Makes basic item JSON files
:: Requires a _standard_item.json to be present for parenting
:: Can make multiple items at once
::
:: Usage:
:: _make (item name 1) (item name 2) (item name x)
::
:: Change this to your mod's ID
set modid=fruitfulfun

setlocal enabledelayedexpansion

for %%x in (%*) do (
    echo Making %%x_leaves.json
    (
        echo {
        echo   "parent": "%modid%:block/leaves_%%x"
        echo }
    ) > %%x_leaves.json

)
