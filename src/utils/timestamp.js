/**
 * 时间戳处理工具
 * 统一处理前端日期时间格式，确保向后端传递正确的时间戳
 */

/**
 * 确保值为时间戳数字
 */
export const ensureTimestamp = (value) => {
    if (!value) return null

    console.log('ensureTimestamp 输入:', value, typeof value)

    // 如果是数字，直接返回
    if (typeof value === 'number') {
        return value
    }

    // 如果是字符串，尝试转换
    if (typeof value === 'string') {
        // 检查是否是数字字符串
        if (/^\d+$/.test(value)) {
            const num = parseInt(value, 10)
            console.log('转换为数字:', num)
            return num
        }

        // 检查是否是无效字符串（如 "ti0e0tam0p"）
        if (value.includes('ti0e0tam0p') || value.includes('timestamp')) {
            console.warn('检测到无效的时间格式，返回 null')
            return null
        }

        // 尝试作为日期字符串解析
        try {
            const timestamp = new Date(value).getTime()
            if (!isNaN(timestamp)) {
                console.log('日期字符串转换为时间戳:', timestamp)
                return timestamp
            }
        } catch (e) {
            console.error('日期解析错误:', e)
        }

        console.error('无法转换为时间戳的字符串:', value)
        return null
    }

    // 如果是 Date 对象
    if (value instanceof Date) {
        return value.getTime()
    }

    console.error('不支持的时间格式:', typeof value, value)
    return null
}

/**
 * 处理表单中的时间戳字段
 */
export const processFormTimestamps = (formData) => {
    const processed = { ...formData }
    const timestampFields = [
        'startTime', 'endTime', 'createdAt', 'updatedAt',
        'expiredTime', 'paymentTime', 'exchangeTime'
    ]

    console.log('处理前数据:', processed)

    timestampFields.forEach(field => {
        if (processed[field] !== undefined && processed[field] !== null) {
            const originalValue = processed[field]
            processed[field] = ensureTimestamp(processed[field])
            console.log(`字段 ${field}: ${originalValue} -> ${processed[field]}`)
        }
    })

    console.log('处理后数据:', processed)
    return processed
}

/**
 * 验证时间戳字段
 */
export const validateTimestamps = (formData, requiredFields = []) => {
    const errors = []

    requiredFields.forEach(field => {
        if (!formData[field]) {
            errors.push(`${field} 不能为空`)
        } else if (typeof formData[field] !== 'number') {
            errors.push(`${field} 格式错误`)
        }
    })

    return errors
}

/**
 * 格式化时间为可读字符串（仅用于显示）
 */
export const formatTimeForDisplay = (timestamp) => {
    if (!timestamp) return ''
    return new Date(timestamp).toLocaleString('zh-CN')
}