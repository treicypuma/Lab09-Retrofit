package com.example.lab09

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun ScreenRecetas(navController: NavHostController, servicio: RecipeApiService) {
    var recetas by remember { mutableStateOf<List<RecipeModel>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            recetas = servicio.getRecipes().recipes
        } catch (e: Exception) {
            Log.e("RECETAS", "Error al cargar recetas: ${e.message}")
        } finally {
            cargando = false
        }
    }

    if (cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recetas) { receta ->
                TarjetaReceta(receta) {
                    navController.navigate("recetasVer/${receta.id}")
                }
            }
        }
    }
}

@Composable
fun TarjetaReceta(receta: RecipeModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = receta.image,
                contentDescription = receta.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Column(Modifier.padding(12.dp)) {
                Text(
                    text = receta.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = receta.cuisine,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Calificación",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("${receta.rating} (${receta.reviewCount})", fontSize = 13.sp)
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = receta.difficulty,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colorDificultad(receta.difficulty))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${receta.prepTimeMinutes + receta.cookTimeMinutes} min  •  ${receta.servings} porciones",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

fun colorDificultad(dificultad: String): Color = when (dificultad) {
    "Easy" -> Color(0xFF43A047)
    "Medium" -> Color(0xFFFB8C00)
    else -> Color(0xFFE53935)
}

@Composable
fun ScreenRecetaDetalle(
    navController: NavHostController,
    servicio: RecipeApiService,
    id: Int
) {
    var receta by remember { mutableStateOf<RecipeModel?>(null) }

    LaunchedEffect(id) {
        try {
            receta = servicio.getRecipeById(id)
        } catch (e: Exception) {
            Log.e("RECETAS", "Error al cargar la receta $id: ${e.message}")
        }
    }

    val r = receta
    if (r == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = r.image,
                contentDescription = r.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )

            Column(Modifier.padding(16.dp)) {
                Text(r.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(
                    r.cuisine,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Calificación",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("${r.rating} (${r.reviewCount} opiniones)", fontSize = 14.sp)
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = r.difficulty,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colorDificultad(r.difficulty))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        DatoReceta("Preparación", "${r.prepTimeMinutes} min")
                        DatoReceta("Cocción", "${r.cookTimeMinutes} min")
                        DatoReceta("Porciones", "${r.servings}")
                        DatoReceta("Calorías", "${r.caloriesPerServing}")
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Ingredientes", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                r.ingredients.forEach { ingrediente ->
                    Text(
                        "•  $ingrediente",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))
                Text("Instrucciones", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                r.instructions.forEachIndexed { indice, paso ->
                    Text(
                        "${indice + 1}. $paso",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver a la lista")
                }
            }
        }
    }
}

@Composable
fun DatoReceta(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(titulo, fontSize = 11.sp, color = Color.Gray)
    }
}