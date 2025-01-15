export const validateUsername = (currentValue = '') => {
    return currentValue.length >= 3 ? '' : '-at least 3 characters';
};

export const validateEmail = (currentValue = '') => {
    let errors = [];
    if (!currentValue.includes('@')) {
        errors.push('-one "@" symbol');
    }
    if (!currentValue.includes('.')) {
        errors.push('-at least one "."');
    }

    return errors.join('\n');
};

export const validatePassword = (currentValue = '') => {
    let errors = [];
    if (currentValue.length < 8 || currentValue.length > 100) {
        errors.push('-8 to 100 characters');
    }
    if (!/[A-Z]/.test(currentValue)) {
        errors.push('-uppercase letters');
    }
    if (!/[a-z]/.test(currentValue)) {
        errors.push('-lowercase letters');
    }
    if (!/[0-9]/.test(currentValue)) {
        errors.push('-at least one digit');
    }
    if (!/[!@#$%^&*]/.test(currentValue)) {
        errors.push('-special characters');
    }
    if (/\s/.test(currentValue)) {
        errors.push('-no spaces');
    }
    return errors.join('\n');
};

