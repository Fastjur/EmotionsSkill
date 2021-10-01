package furhatos.app.emotionskill.flow

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import org.json.JSONException
import org.json.JSONObject

val Start : State = state(Interaction) {

    onTime(repeat=3000..5000) {
        parallel {
            println("Getting emotion data!")
            val emotionJson = khttp.get(url = "http://127.0.0.1:5000/emotion").text
            try {
                val obj = JSONObject(emotionJson)
                println(obj)
            } catch (e: JSONException) {
                println("Json syntax exception!")
                println(e)
            }
        }
    }

    onEntry {
        furhat.ask("Hi there. Do you like robots?")
    }

    onResponse<Yes>{
        furhat.say("I like humans.")
    }

    onResponse<No>{
        furhat.say("That's sad.")
    }
}
