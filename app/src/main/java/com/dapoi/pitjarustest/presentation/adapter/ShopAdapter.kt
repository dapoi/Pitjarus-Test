package com.dapoi.pitjarustest.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dapoi.pitjarustest.data.source.remote.model.StoresItem
import com.dapoi.pitjarustest.databinding.ItemListShopBinding

class ShopAdapter : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    private val shopList = ArrayList<StoresItem>()

    var onClick: ((StoresItem) -> Unit)? = null

    fun setShopList(shopList: List<StoresItem>) {
        this.shopList.clear()
        this.shopList.addAll(shopList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = shopList.size

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.apply {
            shopName.text = shopList[position].store_name
            shopAddress.text = shopList[position].address
            shopArea.text = shopList[position].area_name

            if (shopList[position].has_visit) {
                ivCheckVisited.visibility = View.VISIBLE
            } else {
                ivCheckVisited.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onClick?.invoke(shopList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        return ShopViewHolder(
            ItemListShopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ShopViewHolder(
        binding: ItemListShopBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        var shopName = binding.tvShopName
        var shopAddress = binding.tvShopAddress
        var shopArea = binding.tvShopArea
        var ivCheckVisited = binding.ivHasVisit
    }
}