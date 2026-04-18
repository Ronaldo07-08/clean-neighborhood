import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.MobApp.cleanneighborhood.R

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Иконка дерева
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Надпись "ЧИСТЫЙ"
        Text(
            text = "ЧИСТЫЙ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF609432),
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        // Надпись "КВАРТАЛ"
        Text(
            text = "КВАРТАЛ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF609432),
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
    }
}