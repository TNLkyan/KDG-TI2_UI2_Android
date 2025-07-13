package com.example.groeiproject_ui2.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groeiproject_ui2.data.LocalDatasource.localPeople
import com.example.groeiproject_ui2.model.Person
import com.example.groeiproject_ui2.ui.PersonViewModel
import com.example.groeiproject_ui2.R

@Composable
fun VerticalPeopleList(
    onItemClick: (Person) -> Unit,
    personViewModel: PersonViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(personViewModel.uiState.value.people, key = { person -> person.id }) { person ->
            PersonListItem(
                person = person,
                personViewModel = personViewModel,
                onItemClick = { onItemClick(person) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PersonListItem(person: Person, personViewModel: PersonViewModel, modifier: Modifier = Modifier, onItemClick: (Int) -> Unit) {
    Card(
        modifier = modifier.clickable { onItemClick(person.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.secondary
        ),
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer)
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box() {
                var image by remember { mutableStateOf<Bitmap?>(null) }

                LaunchedEffect(key1 = person.fotoUrl) {
                    image = personViewModel.loadImage(person.fotoUrl)
                }

                image?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(8.dp)
            ) {
                Text(
                    text = person.naam,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text("${stringResource(R.string.person_card_description_leeftijd)} ${person.leeftijd}")
                Text("${stringResource(R.string.person_card_description_nationaliteit)} ${person.nationaliteit}")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewPeopleVerticalList() {
    VerticalPeopleList(
        onItemClick = {  },
        personViewModel = viewModel())
}