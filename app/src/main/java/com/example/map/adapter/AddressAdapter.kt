package com.example.map.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.map.databinding.ItemAddressBinding
import com.example.map.model.Address
import java.util.Locale

class AddressAdapter() : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    var onClickAddress: ((address:String) -> Unit)? = null
    private var listAddress: MutableList<Address> = mutableListOf()
    private var keyWord : String = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding : ItemAddressBinding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(listAddress[position],keyWord)
        holder.itemView.setOnClickListener{
            onClickAddress?.invoke(listAddress[position].label)
        }
    }

    override fun getItemCount(): Int {
        return listAddress.size
    }
    class AddressViewHolder(private var itemAddressBinding:ItemAddressBinding) : RecyclerView.ViewHolder(itemAddressBinding.root){

        fun bind(address:Address,name : String){

            val lowerCaseText = address.label.lowercase(Locale.ROOT)
            val lowerCaseSearchText = name.lowercase(Locale.ROOT)

            val startIndex = lowerCaseText.indexOf(lowerCaseSearchText)
           if(startIndex != -1 ){
               val spannableString = SpannableString(address.label)
               spannableString.setSpan(ForegroundColorSpan(Color.BLACK), startIndex, startIndex + name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
               itemAddressBinding.tvContent.text = spannableString
           }else{
               itemAddressBinding.tvContent.text = address.label
           }

        }
    }

    fun addListAddress(data: List<Address>, name: String) {
        val positionStart = listAddress.size
        listAddress.addAll(data)
        keyWord = name
        notifyItemRangeInserted(positionStart, data.size)
    }
      fun clearListAddress() {
        val itemCount = listAddress.size
        listAddress.clear()
        notifyItemRangeRemoved(0, itemCount)
    }
}