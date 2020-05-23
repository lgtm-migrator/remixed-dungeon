---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by mike.
--- DateTime: 5/23/20 11:28 PM
---

local RPD                 = require "scripts/lib/commonClasses"

local spell               = require "scripts/lib/spell"

local distracted          = {}
distracted["SLEEPING"]    = true
distracted["FLEEING"]     = true
distracted["HORRIFIED"]   = true
distracted["RUNNINGAMOK"] = true

return spell.init{
    desc  = function ()
        return {
            image         = 3,
            imageFile     = "spellsIcons/common.png",
            name          = "Backstab_Name",
            info          = "Backstab_Info",
            magicAffinity = "Rogue",
            targetingType = "cell",
            level         = 2,
            castTime      = 1,
            spellCost     = 2
        }
    end,

    castOnCell = function(self, spell, caster, cell)

        local level = caster:level()
        local ownPos = caster:getPos()
        local dist = level:distance(ownPos, cell)

        if ownPos == cell then
            RPD.glogn("Backstab_OnSelf")
            return false
        end

        if dist  > 1 then
            RPD.glogn("Backstab_TooFar")
            return false
        end

        local victim = RPD.Actor:findChar(dst)

        if victim == nil then
            RPD.glogn("Backstab_EmptyCell")
            return false
        end

        if not distracted[victim:getState():getTag()] then
            RPD.glogn("Backstab_Aware")
            return false
        end

        victim:getSprite():showStatus(0xffa07060,"backstab")

        local weapon = caster:getBelongings().weapon
        local damage = caster:skillLevel() * weapon:damageRoll(caster)
        victim:damage(damage, caster)

        return true
    end
}