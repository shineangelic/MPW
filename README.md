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

