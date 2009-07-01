package nz.net.kallisti.emusicj.urls;

import com.google.inject.Provider;

/**
 * <p>
 * Provides some default implementations for URL factories.
 * </p>
 * 
 * @author robin
 */
public abstract class AbstractURLFactory implements IURLFactory {

	private final Provider<IDynamicURL> dynamicUrlProvider;
	private IDynamicURL bannerURL;

	public AbstractURLFactory(Provider<IDynamicURL> dynamicUrlProvider) {
		this.dynamicUrlProvider = dynamicUrlProvider;
	}

	public synchronized IDynamicURL getBannerClickURL() {
		// By default, banner URLs don't have a URL. They get given it later.
		if (bannerURL == null)
			bannerURL = dynamicUrlProvider.get();
		return bannerURL;
	}

}
