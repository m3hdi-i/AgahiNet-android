package ir.m3hdi.agahinet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import ir.m3hdi.agahinet.ui.theme.AppTheme
import androidx.constraintlayout.compose.Dimension
import ir.m3hdi.agahinet.domain.model.chat.Message
import ir.m3hdi.agahinet.domain.model.chat.Contact
import ir.m3hdi.agahinet.ui.components.util.RtlLayout
import ir.m3hdi.agahinet.ui.components.util.SpacerV


@Composable
fun ChatTitle(contact : Contact, lastMessage: Message, onClick:()->Unit) {

    Column(Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {


        Row(modifier = Modifier.fillMaxWidth()) {

            Box(Modifier.size(64.dp), contentAlignment = Alignment.Center){
                Box(modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(color = getProfileColor(contact.id)))

                Text(text = contact.name.firstOrNull()?.toString() ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),verticalArrangement = Arrangement.SpaceBetween){


                ConstraintLayout(modifier = Modifier.wrapContentHeight().fillMaxWidth()) {
                    val (contactNameRef, timeRef) = createRefs()

                    Text(modifier=Modifier.padding(end=8.dp).constrainAs(contactNameRef){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(timeRef.start)
                        width = Dimension.fillToConstraints
                    },text = contact.name,fontWeight = FontWeight.Bold,maxLines=1,overflow = TextOverflow.Ellipsis,
                        color=Color.Black, style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = lastMessage.createdAt?:"", color = Color.Gray, modifier = Modifier.constrainAs(timeRef){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    })
                }

                Row{
                    if (lastMessage.isMyMessage)
                        Text(text = "شما: ", color=Color.Gray)
                    Text(text = lastMessage.text,maxLines=1,overflow = TextOverflow.Ellipsis, color=Color.DarkGray)
                }

            }

        }
        SpacerV(height = 8.dp)
        Divider(Modifier.padding(horizontal = 16.dp))

    }

}

@Preview(device = "id:Galaxy Nexus", showBackground = true)
@Composable
fun PreviewChatScreen(){
    AppTheme {
        RtlLayout {

        }
    }
}

val profileColors by lazy {
    listOf(
        Color(0xFFEF5350),
        Color(0xFFAB47BC),
        Color(0xFF5C6BC0),
        Color(0xFF29B6F6),
        Color(0xFF26A69A),
        Color(0xFF9CCC65),
        Color(0xFFFFEE58),
        Color(0xFFFFA726),
        Color(0xFF8D6E63),
        Color(0xFF78909C)
    )
}

fun getProfileColor(contactId:Int):Color = profileColors[contactId % 10]

