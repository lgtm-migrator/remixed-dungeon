---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by mike.
--- DateTime: 5/2/20 12:24 AM
---

local RPD = require "scripts/lib/commonClasses"

local gameScene = require "scripts/services/gameScene"

local GameControl = luajava.bindClass("com.nyrds.pixeldungeon.utils.GameControl")

local levels = RPD.DungeonGenerator:getLevelsList()
local levelsSize = levels:size()
local currentLevel = 0
local framesOnLevel = 0
local framesOnScene = 0
local prevScene


local service = {}

local function noneMode(self, scene)

end

local function stdModeOnStep(self, scene)
    if scene == "GameScene" then
        gameScene.onStep()
    end
end

local function heroAiStep()
    local hero = RPD.Dungeon.hero

    if not hero:isReady() then
        return
    end

    local activeWindow = RPD.RemixedDungeon:scene():getWindow(0)

    if activeWindow then
        if tostring(activeWindow:getClass()):match('WndInfoMob') then
            local target = activeWindow:getTarget()
            local action = RPD.CharUtils:randomAction(target,hero)
            RPD.CharUtils:execute(target, hero, action);
        end

        activeWindow:hide()
        return
    end

    local level = hero:level()

    if hero:buffLevel('Blindness') > 0 then
        local cell = level:getEmptyCellNextTo(hero:getPos())
        if level:cellValid(cell) then
            hero:handle(cell)
        else
            hero:rest(false)
        end
        return
    end

    if hero:visibleEnemies() > 0 and hero:buffLevel('Charmed') == 0 then
        local enemyPos = hero:visibleEnemy(0):getPos()
        if level:cellValid(enemyPos) then
            hero:handle(enemyPos)
            return
        end
    end

    if hero:buffLevel('Roots') > 0 or math.random() < 0.1 then
        hero:search(true)
        return
    end

    local exitCell = level:getRandomVisibleTerrainCell(RPD.Terrain.EXIT)

    if level:cellValid(exitCell) and not level:getTopLevelObject() then
        hero:handle(exitCell)
        return
    end

    local doorCell = level:getRandomVisibleTerrainCell(RPD.Terrain.DOOR)

    if level:cellValid(doorCell) and  not level:isCellVisited(doorCell) then
        hero:handle(doorCell)
        return
    end

    local cell = -1

    for i = 1,10 do
        cell = level:randomVisibleDestination()
        if not level:isCellVisited(cell) then
            break
        end
    end

    hero:handle(cell)
end

local function levelsTestModeOnStep(self, scene)

    if scene ~= prevScene then
        prevScene = scene
        framesOnScene = 0
    else
        framesOnScene = framesOnScene + 1
    end

    if scene == "GameScene" then

        framesOnLevel = framesOnLevel + 1

        local hero = RPD.Dungeon.hero

        hero:ht(10000)
        hero:hp(hero:ht())

        if hero:myMove() then
            heroAiStep()
        end

        if framesOnLevel > 1000 then
            currentLevel = currentLevel + 1

            if currentLevel < levelsSize then
                framesOnLevel = 0

                local nextLevelId = levels:get(currentLevel)
                RPD.glog("trying level: %s", nextLevelId)
                GameControl:changeLevel(nextLevelId)
            else
                service.onStep = stdModeOnStep
                GameControl:titleScene()
            end
        end
    end

    if scene == "TitleScene" and framesOnScene > 2 then
        levels = RPD.DungeonGenerator:getLevelsList()
        GameControl:startNewGame("WARRIOR", 2, true)
    end
end

local modes = {}
modes["std"] = stdModeOnStep
modes["levelsTest"] = levelsTestModeOnStep

service.onStep = stdModeOnStep

service.setMode = function(self, mode)
    service.onStep = modes[mode] or noneMode
end

service.selectCell = function(self)
    gameScene.selectCell()
end

return service