package com.example.newsapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.newsapplication.databinding.FragmentDetailsBinding


class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    // get fragment parameters from previous fragment
    val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val newsItem = args.newsItem
        val title = newsItem.title.toString()
        val description = newsItem.description.toString()

        // Display image
        Glide.with(root)
            .load(newsItem.newsImage)
            .placeholder(R.drawable.ic_menu_camera) // Optional placeholder image
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imageViewLinkToNews)

        binding.textViewTitle.text = title

        // Make a clickable link after description with the text "Read more"
        val readMore = "Read more"
        val descriptionText = SpannableString(description + " " + readMore)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                val url = newsItem.newsUrl.toString()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
        descriptionText.setSpan(clickableSpan, description.length + 1,
            description.length + readMore.length + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textViewDescription.text = descriptionText
        binding.textViewDescription.movementMethod = LinkMovementMethod.getInstance()

        // Go to news site by clicking the image.
        binding.imageViewLinkToNews.setOnClickListener {
            openNewsUrlInBrowser(newsItem!!.newsUrl.toString())
        }
        return root
    }

    // Open link in browser
    private fun openNewsUrlInBrowser(newsUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        requireContext().startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}