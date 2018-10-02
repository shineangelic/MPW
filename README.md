

## ! App removed !!
Google decided to remove this dangerous app from its store, with following reasons:

> During review, we found that your app, Mining Pool Watcher (it.angelic.mpw), violates the Payments policy.

> Our policy states:

> Developers charging for apps and downloads from Google Play must use Google Play’s payment system.
> Developers offering virtual goods or currencies within a game downloaded from Google Play must use Google Play's in-app billing service as the method of payment.
> For example, your app accepts donations without using Google Play's payment system.

> Donations may only be collected within an app under certain conditions:

> Donations are only permitted for validated non-profit charitable organizations (for example, a validated 501(c)(3) charitable organization or the local equivalent).
> Donations must be facilitated via a web browser, and any collection must be made through a secure payment system.

Since I don't want to take any action, as I believe this policy is broken, you are free to download this app, compile and use it as you prefer. Bye bye Play store

# MPW


Mining Pool Watcher is a generic Android client for [open-ethereum-pool](https://github.com/sammy007/open-ethereum-pool).
It stores on a local SQLite database data retrieved via JSON, in order to show charts and rich miners statistics. It computes estimated earnings and plots payments history.

![Miner Pool Watcher](https://lh3.googleusercontent.com/ycsUtCMMXIM3QneCkj2lIn14w-GwfMa1dVOG_waloShgHg3g3dpVzLo5F37tmXkO3Q=w300-rw)

It's also published on [playconsole](https://play.google.com/store/apps/details?id=it.angelic.mpw). Please tip me if you use it :)

<img alt="Mining Pool Watcher dashboard"   src="https://lh3.googleusercontent.com/sajy7Pq6ze7Pklw9qkMG526e2PmOQFmd9Xsh47Jq-NfUSsx_Pb8bt3Sg1PTp_VSPJg=h310-rw" />|<img alt="Mining Pool Watcher Charts"   src="https://lh3.googleusercontent.com/pUzhj7YY9ubBHVH97oi8KKNLMQgIuTrzPDlDAoxpW7CLFGP-z3R6gOSsoB5DONyt6Js=h310-rw" />|<img alt="Mining Pool Watcher"   src="https://lh3.googleusercontent.com/mGTc3ao97hZ6V5sRKKhyqh16e92VYCksFjrBX3SbklfP8PnpsPAb_uiJf8b98KbZcw=h310-rw" />

## Used Libraries

 * [AndroidCharts](https://github.com/HackPlan/AndroidCharts)
 * [MaterialSpinner](https://github.com/ganfra/MaterialSpinner)
 * [Commons-math](https://github.com/apache/commons-math)
 
## Supported pools
 
 * NoobPool
 * Cryptopool
 * MaxHash
 * XEMiners
 * ChileMiners
 * 2miners
 * Nevermining
 * Kratospool
 * Soyminero
 
## Privacy

Play store requires me to advise you about the fact Mining Pool Watcher is requesting READ_PHONE_STATE permission. Actually, the app has nothing to do with 'phone' and you can check it in the sources. Permission is required by a library, after I had to move to [firebase-job-scheduler](https://github.com/firebase/firebase-jobdispatcher-android) to ensure your blocks finding were reported and your pools payments notified.

## Implementation 

The app makes use of three SQLite tables, used to store wallet, pool and miners data. When a new Pool/currency pair is selected at app startup a new DB will be created, so that data is kept separate among different pools/currencies. Database's structure is dumb by design, storing only the pair date/rawjson. This allow differences among schemas implemented by different pools and leave flexibility to internal representation change.

Blocks section and payments section are not stored on app's database: since the content of those jsons is generally small and those screens are not very used, I saved some dev time not binding recyclerViews to database but just asking JSons to Volley at runtime.

The central class for app configuration is an enum that defines the pool's list. Near there, you'll find CurrencyEnum whose duty should be clear from its name. Mining Pool Watcher also uses a minimal ''Coinmarketcap'' client to refresh mined currencies value in BTC and $.

