package furhatos.app.emotionskill

import furhatos.app.emotionskill.flow.*
import furhatos.skills.Skill
import furhatos.flow.kotlin.*

class EmotionskillSkill : Skill() {
    override fun start() {
        Flow().run(Idle)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
