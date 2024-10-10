package abner.projetos.whatsapp.adapters

import abner.projetos.whatsapp.databinding.ItemContatosBinding
import abner.projetos.whatsapp.model.Usuario
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso

class ContatosAdapter : RecyclerView.Adapter<ContatosAdapter.ContatosViewHolder>() {

    private var listaContatos = emptyList<Usuario>()

    @SuppressLint("NotifyDataSetChanged")
    fun adicionarLista(lista: List<Usuario>) {
        listaContatos = lista
        notifyDataSetChanged()
    }

    inner class ContatosViewHolder(
        private val binding: ItemContatosBinding
    ) : ViewHolder(binding.root) {
        fun bind(usuario: Usuario) {
            binding.tvContatosNome.text = usuario.nome
            Picasso.get()
                .load(usuario.foto)
                .into(binding.imageContatoFoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosViewHolder {
        val infalter = LayoutInflater.from(parent.context)
        val itemView = ItemContatosBinding.inflate(
            infalter, parent, false
        )

        return ContatosViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return listaContatos.size
    }

    override fun onBindViewHolder(holder: ContatosViewHolder, position: Int) {
        val usuario = listaContatos[position]
        holder.bind(usuario)
    }
}