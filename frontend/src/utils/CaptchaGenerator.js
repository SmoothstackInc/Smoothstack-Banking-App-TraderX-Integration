const generateCaptcha = () => {
    const num1 = Math.floor(Math.random() * 10);
    const num2 = Math.floor(Math.random() * 10);
    const question = `${num1} + ${num2}`;
    const answer = num1 + num2;
    return { question, answer };
};

export default generateCaptcha;
