package ai.sridhar.faq

import org.apache.tika.Tika
import org.apache.tika.io.FilenameUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.*

object FileUtils {

    private val tika = Tika()

    fun multipartFileToTempFile(multipartFile: MultipartFile) : File {
        val fileName = multipartFile.originalFilename ?: multipartFile.name
        val tempFile = File.createTempFile(UUID.randomUUID().toString(), FilenameUtils.getSuffixFromPath(fileName))
        FileOutputStream(tempFile).use {
            it.write(multipartFile.inputStream.readAllBytes())
        }
        return tempFile
    }

    fun contentTypeDetector(file: File): String? {
        return tika.detect(file)
    }

}