package com.danmurphyy.bilimcha.navigations

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey as BaseNavKey
import com.danmurphyy.bilimcha.uibases.SheetContent

@Stable
class SheetController {
    private val _sheet = mutableStateOf<SheetContent?>(null)
    val sheet: State<SheetContent?> = _sheet

    var shouldDismiss = mutableStateOf(false)

    fun show(content: SheetContent) {
        shouldDismiss.value = false
        _sheet.value = content
    }

    fun hide() {
        shouldDismiss.value = true
    }

    fun clear() {
        _sheet.value = null
        shouldDismiss.value = false
    }
}

@Stable
class BackStackController {
    private var backStack: NavBackStack<BaseNavKey>? = null
    private val results = mutableMapOf<String, Any?>()

    fun setBackStack(backStack: NavBackStack<BaseNavKey>) {
        this.backStack = backStack
    }

    fun <T> setResult(key: String, result: T) {
        results[key] = result
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getResult(key: String): T? {
        return results.remove(key) as? T
    }

    fun push(key: NavKey) {
        backStack?.add(key)
    }

    fun replace(key: NavKey) {
        backStack?.apply {
            if (isNotEmpty()) removeAt(size - 1)
            add(key)
        }
    }

    fun pop() {
        backStack?.removeLastOrNull()
    }

    fun current(): NavKey? = backStack?.lastOrNull() as? NavKey

    fun popTo(key: NavKey) {
        val stack = backStack ?: return

        val index = stack.indexOfLast { it == key }

        if (index == -1) {
            // Key not found → add it as root
            stack.clear()
            stack.add(key)
        } else {
            // Remove everything above the existing key
            while (stack.size > index + 1) {
                stack.removeAt(stack.size - 1)
            }
        }
    }

    fun clearAndPush(key: NavKey) {
        backStack?.apply {
            clear()
            add(key)
        }
    }
}
