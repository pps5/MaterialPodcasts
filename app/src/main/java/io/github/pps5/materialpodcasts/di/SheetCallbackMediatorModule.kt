package io.github.pps5.materialpodcasts.di

import io.github.pps5.materialpodcasts.view.SheetCallbackMediator
import org.koin.dsl.module.module

val sheetCallbackMediatorModule = module {
    single {
        SheetCallbackMediator()
    }
}