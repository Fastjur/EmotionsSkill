package furhatos.app.emotionskill.flow

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.ConnectException

private fun containsHappiness(emotions: JSONArray): Boolean {
    return emotions.any { it == "Happiness" || it == "Pleasure" }
}

private fun containsConfusion(emotions: JSONArray): Boolean {
    return emotions.any { it == "Doubt/Confusion" }
}

private fun containsAnger(emotions: JSONArray): Boolean {
    return emotions.any { it == "Anger" || it == "Annoyance" }
}

val Start : State = state(Interaction) {

    onTime(repeat=3000) {
        println("Getting emotion data!")
        try {
            val emotionJson = khttp.get(url = "http://127.0.0.1:5000/emotion").text
            try {
                val obj = JSONObject(emotionJson)
                val emotions = obj.getJSONObject("bbox_0").getJSONArray("cat")
                println(emotions)
                if (containsHappiness(emotions)) {
                    // Happiness, Pleasure
                    println("Going to the happy state!")
                    goto(Happy)
                } else if(containsConfusion(emotions)) {
                    // Doubt/Confusion
                    println("Going to the doubt state!")
                    goto(Confusion)
                } else if(containsAnger(emotions)) {
                    // Anger, Annoyance
                    println("Going to the anger state!")
                    goto(Anger)
                }
            } catch (e: JSONException) {
                println("Json syntax exception!")
                println(e)
            }
        } catch (e: ConnectException) {
            println("Python script not running, or could not connect. Exiting")
            exit()
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

val Happy: State = state(Interaction) {
    onEntry {
        furhat.ask("I can see that you are happy, why are you happy?")
    }

    // TODO Exit state, go back to start or something? But then the onTime will trigger again so it keeps going faster
}

val Confusion: State = state(Interaction) {
    onEntry {
        furhat.ask("What are you confused about?")
    }

    // TODO Exit state, go back to start or something? But then the onTime will trigger again so it keeps going faster
}

val Anger: State = state(Interaction) {
    onEntry {
        furhat.ask("Why are you angry?")
    }

    // TODO Exit state, go back to start or something? But then the onTime will trigger again so it keeps going faster
}