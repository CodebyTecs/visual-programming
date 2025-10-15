package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    private lateinit var tvResult: TextView
    private var currentInput: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)

        val btn0: Button = findViewById(R.id.btn0)
        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)
        val btn4: Button = findViewById(R.id.btn4)
        val btn5: Button = findViewById(R.id.btn5)
        val btn6: Button = findViewById(R.id.btn6)
        val btn7: Button = findViewById(R.id.btn7)
        val btn8: Button = findViewById(R.id.btn8)
        val btn9: Button = findViewById(R.id.btn9)
        val btnPlus: Button = findViewById(R.id.btnPlus)
        val btnMinus: Button = findViewById(R.id.btnMinus)
        val btnMultiply: Button = findViewById(R.id.btnMultiply)
        val btnDivide: Button = findViewById(R.id.btnDivide)
        val btnEquals: Button = findViewById(R.id.btnEquals)
        val btnDot: Button = findViewById(R.id.btnDot)

        btn0.setOnClickListener {
            currentInput += "0"
            tvResult.text = currentInput
        }

        btn1.setOnClickListener {
            currentInput += "1"
            tvResult.text = currentInput
        }

        btn2.setOnClickListener {
            currentInput += "2"
            tvResult.text = currentInput
        }

        btn3.setOnClickListener {
            currentInput += "3"
            tvResult.text = currentInput
        }

        btn4.setOnClickListener {
            currentInput += "4"
            tvResult.text = currentInput
        }

        btn5.setOnClickListener {
            currentInput += "5"
            tvResult.text = currentInput
        }

        btn6.setOnClickListener {
            currentInput += "6"
            tvResult.text = currentInput
        }

        btn7.setOnClickListener {
            currentInput += "7"
            tvResult.text = currentInput
        }

        btn8.setOnClickListener {
            currentInput += "8"
            tvResult.text = currentInput
        }

        btn9.setOnClickListener {
            currentInput += "9"
            tvResult.text = currentInput
        }

        btnPlus.setOnClickListener {
            currentInput += "+"
            tvResult.text = currentInput
        }

        btnMinus.setOnClickListener {
            currentInput += "-"
            tvResult.text = currentInput
        }

        btnMultiply.setOnClickListener {
            currentInput += "*"
            tvResult.text = currentInput
        }

        btnDivide.setOnClickListener {
            currentInput += ":"
            tvResult.text = currentInput
        }

        btnDot.setOnClickListener {
            currentInput += "."
            tvResult.text = currentInput
        }

        btnEquals.setOnClickListener {
            try {
                val result = evalExpression(currentInput)
                tvResult.text = result.toString()
                currentInput = result.toString()
            } catch (e: Exception) {
                tvResult.text = "Ошибка"
                currentInput = ""
            }
        }
    }
}
private fun evalExpression(expr: String): Double {
    var opIndex: Int = -1
    var op: Char? = null

    for (i in 1 until expr.length) {
        val c = expr[i]
        if (c == '+' || c == '-' || c == '*' || c == ':') {
            opIndex = i
            op = c
            break
        }
    }
    if (opIndex == -1) return expr.toDouble()

    val left = expr.substring(0, opIndex).toDouble()
    val right = expr.substring(opIndex + 1).toDouble()

    if (op == '+') {
        return left + right
    }
    if (op == '-') {
        return left - right
    }
    if (op == '*') {
        return left * right
    }
    if (op == ':') {
        if (right == 0.0) return expr.toDouble()
        return left / right
    }

    return expr.toDouble()
}