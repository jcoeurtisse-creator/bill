package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate300

@Composable
fun MinimalistInputField(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier,
  isPassword: Boolean = false,
  keyboardType: KeyboardType = KeyboardType.Text,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  trailingIcon: @Composable (() -> Unit)? = null,
  testTag: String = ""
) {
  val shape = RoundedCornerShape(10.dp)
  val borderColor = Slate300

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .clip(shape)
      .background(Color.White)
      .border(width = 1.2.dp, color = borderColor, shape = shape)
      .padding(horizontal = 16.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(modifier = Modifier.weight(1f)) {
        if (value.isEmpty()) {
          Text(
            text = placeholder,
            style = TextStyle(
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = Slate400,
              fontFamily = FontFamily.SansSerif,
              letterSpacing = 0.5.sp
            ),
            maxLines = 1
          )
        }
        BasicTextField(
          value = value,
          onValueChange = onValueChange,
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
          textStyle = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate800,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.5.sp
          ),
          cursorBrush = SolidColor(Slate800),
          visualTransformation = if (isPassword) PasswordVisualTransformation('•') else VisualTransformation.None,
          keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
          keyboardActions = keyboardActions
        )
      }

      if (trailingIcon != null) {
        trailingIcon()
      }
    }
  }
}
