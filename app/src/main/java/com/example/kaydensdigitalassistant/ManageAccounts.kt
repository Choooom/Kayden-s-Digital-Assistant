package com.example.kaydensdigitalassistant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.data.EmployeeDetail
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import com.example.kaydensdigitalassistant.data.CustomerDetail
import com.example.kaydensdigitalassistant.data.CustomerDetailViewModel
import com.example.kaydensdigitalassistant.data.EmployeeDetailViewModel


@Composable
fun ManageAccount(navController: NavController){
    val insets = WindowInsets.systemBars.asPaddingValues()
    var title by remember{ mutableStateOf("Employee") }

    val employeeViewModel = LocalEmployeeViewModel.current
    employeeViewModel.clearEmployeeDetail()

    val customerViewModel = LocalCustomerViewModel.current

    var isAscendingOrder by remember{ mutableStateOf(true) }
    var searchQuery by remember{ mutableStateOf("") }

    val employeeList = employeeViewModel.allEmployees.observeAsState(listOf()).value
    val customerList = customerViewModel.allCustomers.observeAsState(listOf()).value

    val onEdit: (EmployeeDetail) -> Unit = { employee ->
        // Handle the edit action here, e.g., navigate to an edit screen
        navController.navigate("editEmployee/${employee.employeeId}")
    }

    val onDelete: (EmployeeDetail) -> Unit = { employee ->
        // Handle the delete action here, e.g., show a confirmation dialog
        // Example: Delete from the database or update the ViewModel
        employeeViewModel.deleteEmployee(employee)
    }

    val customerLocation: (CustomerDetail) -> Unit = { customer ->

        customerViewModel.showCustomerLocation(customer)
    }

    val onEditCustomer: (CustomerDetail) -> Unit = { customer ->
        // Handle the edit action here, e.g., navigate to an edit screen
        navController.navigate("editEmployee/${customer.customerId}")
    }

    val onDeleteCustomer: (CustomerDetail) -> Unit = { customer ->
        // Handle the delete action here, e.g., show a confirmation dialog
        // Example: Delete from the database or update the ViewModel
        customerViewModel.deleteCustomer(customer)
    }


    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(top = insets.calculateTopPadding()).background(Brush.horizontalGradient(
        colors = listOf(BlueStart, BlueEnd)
    )), contentAlignment = Alignment.TopCenter){
        Box(modifier = Modifier.fillMaxWidth()){
            Text(
                text = title,
                fontFamily = kanit_bold,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier.size(40.dp).align(Alignment.CenterEnd)
            )
        }
        Column(modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.9f).padding(top = 100.dp).clip(RoundedCornerShape(20.dp)).background(Color.White),
            verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f).height(50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Employee",
                        fontFamily = if(title != "Employee")font_notosans_light else font_notosans_bold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                            .clickable { title = "Employee" }
                    )
                    Spacer(
                        modifier = Modifier.fillMaxHeight(0.8f).width(1.dp).background(Color.Black)
                    )
                    Text(
                        text = "Customer",
                        fontFamily = if(title != "Customer")font_notosans_light else font_notosans_bold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 10.dp).clickable { title = "Customer" }
                    )
                }
                Row(modifier = Modifier.fillMaxWidth().height(50.dp).padding(end = 10.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically){
                    Button(
                        onClick = {
                            navController.navigate("addNewAccount")
                        },
                        modifier = Modifier
                            .height(35.dp)
                            .width(120.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueEnd),
                        shape = RectangleShape
                    ) {
                        Text("Add Employee", color = Color.White, fontFamily = font_archivo, fontSize = 10.sp)
                    }

                }
            }

            Row(modifier = Modifier.fillMaxWidth()){

            }

            Column(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.96f).padding(top = 5.dp).clip(RoundedCornerShape(10.dp)).background(Color.Gray), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                if(title == "Employee"){
                    EmployeeTable(
                        employeeList = employeeList,
                        viewModel = employeeViewModel
                    )
                }else if(title == "Customer"){
                    CustomerTable(
                        viewModel = customerViewModel,
                        customerList = customerList,
                        onDelete = onDeleteCustomer,
                        navController = navController,
                        onLocation = customerLocation
                    )
                }
            }
        }
    }
}

@Composable
fun TableHeaderCell(text: String, width: Dp) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Color.White,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 2.dp)
    )
}

@Composable
fun TableCell(text: String, width: Dp) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = Color.Black,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 8.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun EmployeeTable(
    employeeList: List<EmployeeDetail>,
    viewModel: EmployeeDetailViewModel
) {
    val nameWidth = 150.dp
    val idWidth = 150.dp
    val phoneWidth = 150.dp
    val emailWidth = 150.dp
    val actionWidth = 50.dp

    var selectedEmployee by remember { mutableStateOf<EmployeeDetail?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        item {
            LazyColumn {
                item {
                    Row(
                        modifier = Modifier
                            .background(BlueStart)
                            .padding(16.dp)
                    ) {
                        TableHeaderCell("Name", nameWidth)
                        TableHeaderCell("Employee #", idWidth)
                        TableHeaderCell("Email", emailWidth)
                        TableHeaderCell("Password", phoneWidth)
                        TableHeaderCell("Action", actionWidth)
                    }
                }

                items(employeeList) { employee ->
                    Row(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(vertical = 12.dp)
                    ) {
                        TableCell(employee.name, nameWidth)
                        TableCell(employee.employeeId.toString().padStart(5, '0'), idWidth)
                        TableCell(employee.emailAddress, emailWidth)
                        TableCell(employee.password, phoneWidth)
                        Box(modifier = Modifier.width(actionWidth)) {
                            TableActionCell(
                                employee = employee,
                                onEdit = {
                                    selectedEmployee = employee
                                    showEditDialog = true
                                },
                                onDelete = {
                                    selectedEmployee = employee
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && selectedEmployee != null) {
        EditEmployeeDialog(
            employee = selectedEmployee!!,
            onDismiss = { showEditDialog = false },
            viewModel = viewModel
        )
    }

    if (showDeleteDialog && selectedEmployee != null) {
        DeleteEmployeeDialog(
            employee = selectedEmployee!!,
            onDismiss = { showDeleteDialog = false },
            viewModel = viewModel
        )
    }
}

@Composable
fun EditEmployeeDialog(
    employee: EmployeeDetail,
    onDismiss: () -> Unit,
    viewModel: EmployeeDetailViewModel
) {
    var name by remember { mutableStateOf(employee.name) }
    var email by remember { mutableStateOf(employee.emailAddress) }
    var contact by remember { mutableStateOf(employee.contactNumber) }
    var password by remember { mutableStateOf(employee.password) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Employee Details",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    placeholder = { Text(employee.name) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text(employee.emailAddress) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact Number") },
                    placeholder = { Text(employee.contactNumber) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text(employee.password) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val updatedEmployee = employee.copy(
                            name = name,
                            emailAddress = email,
                            contactNumber = contact,
                            password = password
                        )
                        viewModel.updateEmployee(updatedEmployee)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}

@Composable
fun DeleteEmployeeDialog(
    employee: EmployeeDetail,
    onDismiss: () -> Unit,
    viewModel: EmployeeDetailViewModel
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Delete Employee",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Are you sure you want to delete ${employee.name}?",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            viewModel.deleteEmployee(employee)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}


@Composable
fun TableActionCell(
    employee: EmployeeDetail,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.cog),
                contentDescription = "Action",
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    expanded = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}

@Composable
fun CustomerTable(
    customerList: List<CustomerDetail>,
    onDelete: (CustomerDetail) -> Unit,
    navController: NavController,
    onLocation: (CustomerDetail) -> Unit,
    viewModel: CustomerDetailViewModel
) {
    val nameWidth = 150.dp
    val idWidth = 150.dp
    val addressWidth = 150.dp
    val contactWidth = 150.dp
    val actionWidth = 50.dp

    var selectedCustomer by remember { mutableStateOf<CustomerDetail?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLocation by remember{ mutableStateOf(false) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        item {
            LazyColumn {
                item {
                    Row(
                        modifier = Modifier
                            .background(BlueStart)
                            .padding(16.dp)
                    ) {
                        TableHeaderCell("Name", nameWidth)
                        TableHeaderCell("Customer #", idWidth)
                        TableHeaderCell("Address", addressWidth)
                        TableHeaderCell("Contact", contactWidth)
                        TableHeaderCell("Action", actionWidth)
                    }
                }

                items(customerList) { customer ->
                    Row(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(vertical = 12.dp)
                    ) {
                        TableCell(customer.name, nameWidth)
                        TableCell(customer.customerId.toString().padStart(5, '0'), idWidth)
                        TableCell(customer.address, addressWidth)
                        TableCell(customer.contactNumber, contactWidth)
                        Box(modifier = Modifier.width(actionWidth)) {
                            CustomerActionCell(
                                customer = customer,
                                onEdit = {
                                    selectedCustomer = customer
                                    showEditDialog = true
                                },
                                onDelete = {
                                    selectedCustomer = customer
                                    showDeleteDialog = true
                                },
                                onLocation = {
                                    selectedCustomer = customer
                                    showLocation = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && selectedCustomer != null) {
        EditCustomerDialog(
            customer = selectedCustomer!!,
            onDismiss = { showEditDialog = false },
            viewModel = viewModel
        )
    }

    if (showDeleteDialog && selectedCustomer != null) {
        DeleteCustomerDialog(
            customer = selectedCustomer!!,
            onDismiss = { showDeleteDialog = false },
            viewModel = viewModel
        )
    }

    if (showLocation && selectedCustomer != null) {
        navController.navigate("map/${selectedCustomer!!.customerId}")
        showLocation = false
    }
}

@Composable
fun CustomerActionCell(
    customer: CustomerDetail,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onLocation: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(id = R.drawable.cog),
                contentDescription = "Action",
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    expanded = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
            DropdownMenuItem(
                text = { Text("Location") },
                onClick = {
                    expanded = false
                    onLocation()
                }
            )
        }
    }
}


@Composable
fun EditCustomerDialog(
    customer: CustomerDetail,
    onDismiss: () -> Unit,
    viewModel: CustomerDetailViewModel
) {
    var name by remember { mutableStateOf(customer.name) }
    var address by remember { mutableStateOf(customer.address) }
    var contactNumber by remember { mutableStateOf(customer.contactNumber) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Customer Details",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    placeholder = { Text(customer.name) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    placeholder = { Text(customer.address) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contactNumber,
                    onValueChange = { contactNumber = it },
                    label = { Text("Contact Number") },
                    placeholder = { Text(customer.contactNumber) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val updatedCustomer = customer.copy(
                            name = name,
                            address = address,
                            contactNumber = contactNumber
                        )
                        viewModel.updateCustomer(updatedCustomer)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}

@Composable
fun DeleteCustomerDialog(
    customer: CustomerDetail,
    onDismiss: () -> Unit,
    viewModel: CustomerDetailViewModel
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Delete Customer",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Are you sure you want to delete ${customer.name}?",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            viewModel.deleteCustomer(customer)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
