package com.marvinboots.harveydentcalculator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_engi.*
import net.objecthunter.exp4j.ExpressionBuilder

class Engi : AppCompatActivity() {

    // TextView used to display the input and output
    lateinit var txtInput: TextView

    // Represent whether the lastly pressed key is numeric or not
    var lastNumeric: Boolean = false

    // Represent that current state is in error or not
    var stateError: Boolean = false

    // If true, do not allow to add another DOT
    var lastDot: Boolean = false

    var lastFunction: Boolean = false

    var lastOperator: Boolean = false

    var brCount: Int = 0

    var lastBracket: Boolean = false

    var clear: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_engi)
        txtInput = findViewById(R.id.txtInput)
        //setSupportActionBar(toolbar)

    }

    fun changeActivity(view: View)
    {
        if(view.getId() == R.id.btnChange) {
            val intent = Intent(this@Engi, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Append the Button.text to the TextView
     */
    fun onDigit(view: View) {
        if (stateError) {
            // If current state is Error, replace the error message
            txtInput.text = (view as Button).text
            stateError = false
        } else if(!stateError && !lastFunction){// && (lastBracket || lastOperator || clear)) {
            // If not, already there is a valid expression so append to it
            txtInput.append((view as Button).text)
            lastNumeric = true
            lastFunction = false
            lastBracket = false
            lastOperator = false
            clear = false
        }
        // Set the flag

    }

    /**
     * Append . to the TextView
     */
    fun onDecimalPoint(view: View) {
        if (lastNumeric && !stateError && !lastDot) {
            txtInput.append(".")
            lastNumeric = false
            lastDot = true
            lastBracket = false
            lastOperator = false
        }
    }

    /**
     * Append +,-,*,/ operators to the TextView
     */
    fun onOperator(view: View) {
        if (!lastOperator && !stateError && !clear) {
            txtInput.append((view as Button).text)
            lastNumeric = false
            lastFunction = false
            lastDot = false    // Reset the DOT flag
            lastBracket = false
            lastOperator = true
        }
    }

    fun onFunction(view: View){
        if(!lastNumeric && !lastFunction && !stateError)
        {
            txtInput.append((view as Button).text)
            lastFunction = true
            lastDot = false    // Reset the DOT flag
            clear = false
            lastOperator = false
            lastBracket = false
        }
    }

    fun onCloseBracket(view: View){
        if((lastNumeric || lastFunction) && !stateError && brCount > 0){
            txtInput.append((view as Button).text)
            lastNumeric = false
            lastFunction = false
            lastDot = false    // Reset the DOT flag
            lastOperator = false
            lastBracket = true
            brCount -=1
        }
    }

    fun onOpenBracket(view: View){
        if( !stateError && !lastNumeric){
            txtInput.append((view as Button).text)
            lastFunction = false
            lastDot = false    // Reset the DOT flag
            lastOperator = false
            lastBracket = true
            brCount +=1
            clear = false
        }
    }

    /**
     * Clear the TextView
     */
    fun onClear(view: View) {
        this.txtInput.text = ""
        lastNumeric = false
        lastBracket = false
        lastFunction = false
        lastOperator = false
        stateError = false
        lastDot = false
        brCount = 0
        clear = true
    }

    /**
     * Calculate the output using Exp4j
     */
    fun onEqual(view: View) {
        // If the current state is error, nothing to do.
        // If the last input is a number only, solution can be found.
        if (!lastOperator && !stateError && brCount == 0) {
            // Read the expression
            val txt = txtInput.text.toString()
            // Create an Expression (A class from exp4j library)
            val expression = ExpressionBuilder(txt).build()
            try {
                // Calculate the result and display
                val result = expression.evaluate()
                txtInput.text = result.toString()
                lastDot = true // Result contains a dot
            } catch (ex: ArithmeticException) {
                // Display an error message
                txtInput.text = "Error"
                stateError = true
                lastNumeric = false
                lastFunction = false

            }
        }
    }
}
