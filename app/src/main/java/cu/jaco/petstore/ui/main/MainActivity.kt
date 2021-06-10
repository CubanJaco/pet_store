package cu.jaco.petstore.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import cu.jaco.petstore.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(ApiViewModel::class.java)

        val agenciesButton = findViewById<AppCompatButton>(R.id.get_agencies)
        agenciesButton.setOnClickListener {
            viewModel.getPetsInventory().observe(this, ::renderInventory)
        }

    }

    private fun renderInventory(inventory: Map<String, Int>) {
        Toast.makeText(
            this,
            "There are ${inventory["available"]} pets available",
            Toast.LENGTH_SHORT
        ).show()
    }

}