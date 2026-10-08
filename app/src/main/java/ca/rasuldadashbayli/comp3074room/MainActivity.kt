package ca.rasuldadashbayli.comp3074room

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)

        setContent {

            val customerViewModel: CustomerViewModel = viewModel(
                factory = CustomerViewModelFactory(
                    customerDao = database.customerDao()
                )
            )

            CustomerApp(customerViewModel)
        }
    }
}

@Composable
fun CustomerApp(
    viewModel: CustomerViewModel
) {

    val customers by viewModel.customers.collectAsState(
        initial = emptyList()
    )

    var name by remember {
        mutableStateOf("")
    }

    var ageText by remember {
        mutableStateOf("")
    }

    var isActive by remember {
        mutableStateOf(false)
    }

    MaterialTheme {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Room Customer Demo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Customer Management",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Customer name")
                        },
                        singleLine = true
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    OutlinedTextField(
                        value = ageText,
                        onValueChange = {
                            ageText = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Age")
                        },
                        singleLine = true
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Active customer",
                            fontWeight = FontWeight.Medium
                        )

                        Switch(
                            checked = isActive,
                            onCheckedChange = {
                                isActive = it
                            }
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Button(
                        onClick = {

                            val age = ageText.toIntOrNull()

                            if (
                                name.isNotBlank() &&
                                age != null
                            ) {

                                viewModel.addCustomer(
                                    name = name.trim(),
                                    age = age,
                                    isActive = isActive
                                )

                                name = ""
                                ageText = ""
                                isActive = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Text(
                            text = "Add Customer",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = "Customers (${customers.size})",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    if (customers.isEmpty()) {

                        Text(
                            text = "No customers in the database.",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )

                    } else {

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {

                            items(
                                items = customers,
                                key = {
                                    it.id
                                }
                            ) { customer ->

                                CustomerRow(
                                    customer = customer,
                                    onDelete = {
                                        viewModel.deleteCustomer(customer)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerRow(
    customer: Customer,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "ID: ${customer.id}  •  Age: ${customer.age}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = if (customer.isActive) {
                        "Active customer"
                    } else {
                        "Inactive customer"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            TextButton(
                onClick = onDelete
            ) {

                Text(
                    text = "Delete"
                )
            }
        }
    }
}x