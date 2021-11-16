package com.example.camerademo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.camerademo.databinding.AdapterMainItemBinding

class MainAdapter(private val menu : MutableList<String>) : RecyclerView.Adapter<MainAdapter.MyHolder>() {

    private var mOnItemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemBinding : AdapterMainItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.adapter_main_item,parent,false)
        return MyHolder.getInstance(itemBinding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bind(getItem(position))
        holder.setOnClickListener(position,mOnItemClickListener)
    }

    override fun getItemCount(): Int {
        return menu.size
    }

    private fun getItem(position: Int) : String{
        return menu[position]
    }

    class MyHolder(val mBinding: AdapterMainItemBinding) : RecyclerView.ViewHolder(mBinding.root){
        companion object{
            fun getInstance(binding : AdapterMainItemBinding) : MyHolder{
                return MyHolder(binding)
            }
        }

        fun bind(data : String){
            mBinding.itemData = data
        }

        fun setOnClickListener(position: Int,onItemClickListener: OnItemClickListener?){
            mBinding.root.setOnClickListener { onItemClickListener?.onItemClick(position) }
        }
    }

    fun setOnItemClickListener(onItemClickListener : OnItemClickListener){
        mOnItemClickListener = onItemClickListener
    }

    interface OnItemClickListener{
        fun onItemClick(position : Int)
    }
}
