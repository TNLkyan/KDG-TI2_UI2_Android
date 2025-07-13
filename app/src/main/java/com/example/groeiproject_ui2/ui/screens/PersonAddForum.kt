package com.example.groeiproject_ui2.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groeiproject_ui2.ui.PersonViewModel
import com.example.groeiproject_ui2.R
import androidx.compose.material3.Switch
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.Alignment

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonAddForum(
    personViewModel: PersonViewModel = viewModel(),
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var statusExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Naam
        OutlinedTextField(
            value = personViewModel.newNaam,
            onValueChange = { personViewModel.newNaam = it },
            label = { Text(stringResource(R.string.person_card_description_naam)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Leeftijd
        OutlinedTextField(
            value = personViewModel.newLeeftijd,
            onValueChange = { personViewModel.newLeeftijd = it.filter { c -> c.isDigit() } },
            label = { Text(stringResource(R.string.person_card_description_leeftijd)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Datum
        OutlinedTextField(
            value = personViewModel.newGeboortedatum,
            onValueChange = { personViewModel.newGeboortedatum = it },
            label = { Text(stringResource(R.string.add_forum_geboortedatum)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Nationaliteit
        OutlinedTextField(
            value = personViewModel.newNationaliteit,
            onValueChange = { personViewModel.newNationaliteit = it },
            label = { Text(stringResource(R.string.person_card_description_nationaliteit)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Lengte
        OutlinedTextField(
            value = personViewModel.newLengte,
            onValueChange = { personViewModel.newLengte = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text(stringResource(R.string.person_card_description_lengte)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Premium klant
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.person_card_description_premium_klant),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = personViewModel.newPremiumKlant,
                onCheckedChange = { isChecked ->
                    personViewModel.newPremiumKlant = isChecked
                }
            )
        }

        // Klant status
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            OutlinedTextField(
                value = personViewModel.klantStatus.name,
                onValueChange = {},
                label = { Text(stringResource(R.string.person_card_description_klant_status)) },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.klant_status_description),
                        modifier = Modifier.clickable { statusExpanded = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { statusExpanded = true }
            )

            DropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                com.example.groeiproject_ui2.model.ClientStatuses.entries.forEach { statusEnum ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = statusEnum.name,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        onClick = {
                            personViewModel.klantStatus = statusEnum
                            statusExpanded = false
                        }
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { personViewModel.saveNewPerson() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.save_person),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.width(40.dp))

            IconButton(
                onClick = {
                    personViewModel.resetForm()
                    onCloseClick()
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cancel_add_person),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}