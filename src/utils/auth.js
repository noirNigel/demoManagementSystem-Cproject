// 简单的认证状态管理
class AuthState {
    constructor() {
        this._isLoggedIn = !!localStorage.getItem('token');
        this.listeners = [];
    }

    get isLoggedIn() {
        return this._isLoggedIn;
    }

    set isLoggedIn(value) {
        if (this._isLoggedIn !== value) {
            this._isLoggedIn = value;
            this.notifyListeners();
        }
    }

    update() {
        const newValue = !!localStorage.getItem('token');
        if (this._isLoggedIn !== newValue) {
            this._isLoggedIn = newValue;
            this.notifyListeners();
        }
    }

    addListener(listener) {
        this.listeners.push(listener);
    }

    removeListener(listener) {
        const index = this.listeners.indexOf(listener);
        if (index > -1) {
            this.listeners.splice(index, 1);
        }
    }

    notifyListeners() {
        this.listeners.forEach(listener => {
            try {
                listener(this._isLoggedIn);
            } catch (error) {
                console.error('Error in auth state listener:', error);
            }
        });
    }
}

// 创建全局实例
export const authState = new AuthState();