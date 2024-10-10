package abner.projetos.whatsapp

import abner.projetos.whatsapp.databinding.ActivityPerfilBinding
import abner.projetos.whatsapp.utils.exibirMensagem
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {

    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false

    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    //Firebase Auth
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    //Storage
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    //Fire Store
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            binding.imagePerfil.setImageURI(uri)
            uploadImagemStorage(uri)
        } else {
            exibirMensagem("Nenhuma imagem selecionada")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        solicitarPermissoes()
        inicializarEventosCliques()
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciaisUsuario()
    }

    private fun recuperarDadosIniciaisUsuario() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario != null) {
            firestore
                .collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val dadosUsuarios = documentSnapshot.data
                    if (dadosUsuarios != null) {
                        val nome = dadosUsuarios["nome"] as String
                        val foto = dadosUsuarios["foto"] as String

                        binding.editNomePerfil.setText(nome)
                        if (foto.isNotEmpty()) {
                            Picasso.get()
                                .load(foto)
                                .into(binding.imagePerfil)
                        }
                    }

                }
        }
    }

    private fun uploadImagemStorage(uri: Uri) {
        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario != null) {
            storage.getReference("fotos")
                .child("usuarios")
                .child("id")
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener { task ->
                    exibirMensagem("Sucesso ao fazer upload da imagem")
                    task.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener { url ->
                            val dados = mapOf("foto" to url.toString())
                            atualizarDadosPerfil(idUsuario, dados)
                        }

                }.addOnFailureListener {
                    exibirMensagem("Erro ao fazer upload da imagem")
                }
        }
    }

    private fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>) {
        firestore
            .collection("usuarios")
            .document(idUsuario)
            .update(dados)
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao atualizar dados do usuário")
            }.addOnFailureListener {
                exibirMensagem("Erro ao atualizar dados do usuário")
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun inicializarEventosCliques() {
        binding.fabSelecionar.setOnClickListener {
            if (temPermissaoGaleria) {
                gerenciadorGaleria.launch("image/*")
            } else {
                exibirMensagem("Você não tem permissão para acessar a galeria")
                solicitarPermissoes()
            }
        }

        binding.btnAtualizarPerfil.setOnClickListener {
            val nomeUsuario = binding.editNomePerfil.text.toString()
            if (nomeUsuario.isNotEmpty()) {
                val idUsuario = firebaseAuth.currentUser?.uid
                if (idUsuario != null) {
                    val dados = mapOf("nome" to nomeUsuario)
                    atualizarDadosPerfil(idUsuario, dados)
                }
            } else {
                exibirMensagem("Preencha o seu nome para atualizar.")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun solicitarPermissoes() {
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        val listaPermissoesNegadas = mutableListOf<String>()
        if (!temPermissaoCamera)
            listaPermissoesNegadas.add(Manifest.permission.CAMERA)
        if (!temPermissaoGaleria)
            listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)
        if (listaPermissoesNegadas.isNotEmpty()) {
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissoes ->

                temPermissaoCamera =
                    permissoes[Manifest.permission.CAMERA] ?: temPermissaoCamera
                temPermissaoGaleria =
                    permissoes[Manifest.permission.READ_MEDIA_IMAGES] ?: temPermissaoGaleria

            }
            gerenciadorPermissoes.launch(listaPermissoesNegadas.toTypedArray())
        }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbarPerfil.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}