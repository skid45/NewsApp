package com.skid.article

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.method.LinkMovementMethodCompat
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import coil.load
import com.skid.article.databinding.FragmentArticleBinding
import com.skid.article.di.ArticleComponentViewModel
import com.skid.news.model.Article
import com.skid.utils.Constants.ARTICLE_KEY
import com.skid.utils.collectFlow
import com.skid.utils.customGetSerializable
import com.skid.utils.format
import javax.inject.Inject
import javax.inject.Provider

class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val article
        get() = checkNotNull(requireArguments().customGetSerializable<Article>(ARTICLE_KEY))

    @Inject
    lateinit var viewModelProvider: Provider<ArticleViewModel.Factory>
    private val articleViewModel: ArticleViewModel by viewModels {
        ArticleViewModel.viewModelFactory(
            assistedFactory = viewModelProvider.get(),
            url = article.url
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        ViewModelProvider(this)
            .get<ArticleComponentViewModel>()
            .articleComponent
            .inject(this)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupArticle(article)
        setupToolbar()
        setupIsSavedArticleCollector()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun setupArticle(article: Article) = with(binding) {
        articleProfileToolbarImage.load(article.imageUrl)
        articleProfileToolbar.title = article.title
        articleProfileName.text = article.title
        articleProfilePublishedAt.text =
            article.publishedAt.format("MMM dd, yyyy | hh:mm a")
        articleProfileSourceName.text = article.sourceName

        articleProfileContent.text = article.content.toSpannable().apply {
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                        startActivity(browserIntent)
                    }
                },
                article.content.lastIndexOf(". ").takeIf { it != -1 }?.plus(2) ?: 0,
                article.content.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        articleProfileContent.movementMethod = LinkMovementMethodCompat.getInstance()
    }

    private fun setupToolbar() = with(binding) {
        articleProfileToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        articleProfileToolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.article_profile_menu_save) {
                if (articleViewModel.isArticleSaved.value) {
                    articleViewModel.deleteArticle(article.url)
                } else articleViewModel.saveArticle(article)
                true
            } else false
        }
    }

    private fun setupIsSavedArticleCollector() = with(binding) {
        collectFlow(articleViewModel.isArticleSaved) { isSaved ->
            val menuItem = articleProfileToolbar.menu.findItem(R.id.article_profile_menu_save)
            menuItem.icon = AppCompatResources.getDrawable(
                requireContext(),
                if (isSaved) com.skid.ui.R.drawable.ic_bookmark
                else com.skid.ui.R.drawable.ic_bookmark_border
            )
        }
    }

}