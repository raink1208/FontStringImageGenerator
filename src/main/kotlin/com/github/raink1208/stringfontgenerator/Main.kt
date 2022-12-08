package com.github.raink1208.stringfontgenerator

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.utils.FileUpload
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

class Main: ListenerAdapter() {
    private val backColor = Color.WHITE
    private val fontColor = Color.BLACK
    private val fontSize = 50f

    private val imageMargin = 4
    private val imageWidth = fontSize * 10 + imageMargin
    private val imageHeight = fontSize + imageMargin

    private val outputPath = Paths.get("./output/")

    init {
        if (!Files.exists(outputPath))
            Files.createDirectory(outputPath)
    }

    lateinit var jda: JDA

    fun start() {
        jda = JDABuilder.createDefault("TOKEN")
            .addEventListeners(this)
            .build()
        jda.updateCommands().addCommands(
            Commands.slash("generate", "沙花叉フォントで文字列を生成する").addOption(OptionType.STRING, "str", "生成する文字", true)
        ).queue()
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val input = event.getOption("str")
        if (input == null) {
            event.reply("生成する文字列を入力してください").queue()
            return
        }
        val file = generateImage(input.asString)
        event.replyFiles(FileUpload.fromData(file)).queue()
    }

    fun generateImage(str: String): File {
        val file = File(outputPath.toString(), "$str.png")
        if (file.exists()) return file
        file.createNewFile()
        val font = Font.createFont(Font.TRUETYPE_FONT, File("./src/main/resources/沙花叉クロヱ50音.ttf")).deriveFont(fontSize)

        val img = BufferedImage(imageWidth.toInt(), imageHeight.toInt(), BufferedImage.TYPE_3BYTE_BGR)
        val g = img.graphics

        g.color = backColor
        g.fillRect(0, 0, img.width, img.height)

        g.color = fontColor
        g.font = font
        val start = fontSize + (imageMargin / 2)
        g.drawString(str, imageMargin/2, start.toInt())
        g.dispose()

        ImageIO.write(img, "png", file)
        return file
    }
}

fun main() {
    Main().start()
}