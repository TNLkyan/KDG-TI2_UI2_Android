package com.example.groeiproject_ui2.ui.screens

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groeiproject_ui2.ui.PersonViewModel
import com.example.groeiproject_ui2.R
import com.example.groeiproject_ui2.model.BankAccount
import com.example.groeiproject_ui2.model.Person


@Composable
fun PersonCardLayout(
    modifier: Modifier = Modifier,
    personViewModel: PersonViewModel = viewModel(),
    onAddForumClick: () -> Unit
) {
    val personUiState by personViewModel.uiState.collectAsState()

    if (!personUiState.IsAddingPerson) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                DropDownHorizontalList(
                    isExpanded = personUiState.isBankAccountsExpanded,
                    personViewModel = personViewModel,
                    bankAccs = personUiState.bankAccs
                )
            }

            Row(
                modifier = Modifier
                    .weight(3f)
                    .padding(20.dp),

                horizontalArrangement = Arrangement.Center
            ) {
                PersonCardImage(personViewModel, personUiState.person.fotoUrl)
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                PersonCardDescription(
                    person = personUiState.person,
                    amountOfBankAccs = personUiState.bankAccs.size,
                    personViewModel = personViewModel,
                    inUpdateMode = personUiState.inUpdateMode
                )
            }
            AnimatedContent(
                targetState = personUiState.isExtraOptionsExpended,
                transitionSpec = {
                    if (targetState) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }.using(SizeTransform(clip = false))
                },
                modifier = Modifier.padding(14.dp).fillMaxWidth()
            ) { isExpanded ->
                if (!isExpanded) {
                    PersonCardButtons(
                        changeIndexPlus = { personViewModel.nextPerson() },
                        changeIndexMinus = { personViewModel.previousPerson() },
                        toggleExtraOptions = { personViewModel.toggleExtraOptions() },
                        modifier = Modifier.padding(4.dp)
                    )

                } else {

                    PersonCardExtraOptionsButtons(
                        goToAddForum = { onAddForumClick() },
                        toggleExtraOptions = { personViewModel.toggleExtraOptions() },
                        deleteFunction = { personViewModel.deletePerson() },
                        modifier = Modifier.padding(4.dp)
                    )

                }
            }
        }
    }
}



@Composable
fun DropDownHorizontalList(
    isExpanded: Boolean,
    personViewModel: PersonViewModel,
    bankAccs: List<BankAccount>
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .animateContentSize()
    ) {
        IconButton(
            onClick = { personViewModel.toggleExpandBankAccounts() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription =  stringResource(R.string.bankrekeningen_icon_expand_toggle),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(R.string.bankrekeningen))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    LazyRow(
                        horizontalArrangement = if (bankAccs.size == 1) Arrangement.Center else Arrangement.Start,
                    ) {
                        items(bankAccs) { bankrek ->
                            DropDownHorizontalListItem(bankrek)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropDownHorizontalListItem(
    bankAcc: BankAccount,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = Color.Black
        ),
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier.padding(4.dp)
    ) {
        DropDownHorizontalListItemDetail(R.string.bankrekeningen_banknaam, bankAcc.banknaam)
        DropDownHorizontalListItemDetail(
            R.string.bankrekeningen_rekeningnummer,
            bankAcc.rekeningnummer
        )
        DropDownHorizontalListItemDetail(R.string.bankrekeningen_saldo, bankAcc.saldo.toString())
        DropDownHorizontalListItemDetail(R.string.bankrekeningen_valuta, bankAcc.valuta)
        DropDownHorizontalListItemDetail(R.string.bankrekeningen_limiet, bankAcc.limiet.toString())
        DropDownHorizontalListItemDetail(
            R.string.bankrekeningen_aangemaakt_op,
            bankAcc.aangemaakt_op.toString()
        )
        DropDownHorizontalListItemDetail(
            R.string.bankrekeningen_flagged,
            bankAcc.flagged.toString()
        )
    }
    Spacer(modifier = Modifier.width(8.dp))
}

@Composable
fun DropDownHorizontalListItemDetail(
    @StringRes label: Int,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.Absolute.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = stringResource(label), fontWeight = FontWeight.Bold)
        Text(value)
    }
}



@Composable
fun PersonCardImage(personViewModel: PersonViewModel, fotoUrl: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var image by remember { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(key1 = fotoUrl) {
            image = personViewModel.loadImage(fotoUrl)
        }

        image?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}



@Composable
fun PersonCardDescription(
    person: Person,
    amountOfBankAccs: Int,
    personViewModel: PersonViewModel,
    inUpdateMode: Boolean
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isLandscape) 75.dp else 140.dp)
            .padding(horizontal = 20.dp, vertical = 1.dp)
            .verticalScroll(scrollState, enabled = isLandscape),
        verticalArrangement = if (isLandscape) Arrangement.Top else Arrangement.Bottom
    ) {
        PersonCardDescriptionLine(
            R.string.person_card_description_naam,
            person.naam
        )
        if (!inUpdateMode)
            PersonCardDescriptionLine(
                label = R.string.person_card_description_leeftijd,
                value = person.leeftijd.toString(),
                enableUpdateMode = { personViewModel.toggleEditMode() }
            )
        else
            UpdateModeField(
                personViewModel = personViewModel,
                value = person.leeftijd.toString(),
            )

        PersonCardDescriptionLine(
            label = R.string.person_card_description_geboortedatum,
            value = person.geboortedatum.toString()
        )
        PersonCardDescriptionLine(
            label = R.string.person_card_description_nationaliteit,
            value = person.nationaliteit
        )
        PersonCardDescriptionLine(
            label = R.string.person_card_description_lengte,
            value = person.lengte.toString()
        )
        PersonCardDescriptionLine(
            label = R.string.person_card_description_premium_klant,
            value = when (person.premiumKlant) {
                true -> "Ja"
                else -> "Nee"
            }
        )
        PersonCardDescriptionLine(
            label = R.string.person_card_description_klant_status,
            value = person.klantStatus.toString()
        )
        PersonCardDescriptionLine(
            label = R.string.person_card_description_aantal_bankrek,
            value = amountOfBankAccs.toString()
        )
    }
}

@Composable
fun PersonCardDescriptionLine(
    @StringRes label: Int,
    value: String,
    enableUpdateMode: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = stringResource(label), fontWeight = FontWeight.Bold)


        Text(text = value)
        if (stringResource(label) == stringResource(R.string.person_card_description_leeftijd)) {
            IconButton(
                onClick = { enableUpdateMode() },
                modifier = Modifier
                    .size(48.dp)
                    .padding(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.leeftijd_update),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun UpdateModeField(
    personViewModel: PersonViewModel,
    value: String,
) {
    var editedLeeftijd by remember { mutableStateOf(value) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            label = { Text(stringResource(R.string.leeftijd_update)) },
            value = editedLeeftijd.toString(),
            onValueChange = { editedLeeftijd = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
        )

        IconButton(
            onClick = {
                editedLeeftijd.toIntOrNull()?.let {
                    personViewModel.updatePersonLeeftijd(editedLeeftijd.toInt())

                }
                personViewModel.toggleEditMode()
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.leeftijd_update),
            )
        }
    }
}



@Composable
fun PersonCardButtons(
    changeIndexPlus: () -> Unit,
    changeIndexMinus: () -> Unit,
    toggleExtraOptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = changeIndexMinus,
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
        ) {
            Text(text = stringResource(R.string.previous))
        }

        Spacer(Modifier.weight(0.1f))

        IconButton(
            onClick = toggleExtraOptions,
            modifier = Modifier
                .size(48.dp)
                .weight(0.8f)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.extra_options_toggle),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(Modifier.weight(0.1f))

        Button(
            onClick = changeIndexPlus,
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
        ) {
            Text(stringResource(R.string.next))
        }
    }
}

@Composable
fun PersonCardExtraOptionsButtons(
    goToAddForum: () -> Unit,
    toggleExtraOptions: () -> Unit,
    deleteFunction: () -> Unit,
    inAddingForum: Boolean = false,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        IconButton(
            onClick = { goToAddForum() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector =  if (!inAddingForum) Icons.Default.Add else Icons.Default.Check,
                contentDescription =  stringResource(R.string.extra_options_add),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(Modifier.weight(0.1f))

        IconButton(
            onClick = { toggleExtraOptions() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector =  Icons.Default.MoreVert,
                contentDescription =  stringResource(R.string.extra_options_toggle),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(Modifier.weight(0.1f))

        IconButton(
            onClick = { deleteFunction() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector =  Icons.Default.Delete,
                contentDescription =  stringResource(R.string.extra_options_delete),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewPersonCardLayout() {
    PersonCardLayout(
        onAddForumClick = {  },
    )
}