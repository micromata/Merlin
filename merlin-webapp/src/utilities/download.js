const downloadFile = (blob, filename) => {
    const a = document.createElement('a');
    a.style = {
        display: 'none'
    };
    a.href = URL.createObjectURL(blob);
    a.download = filename;

    document.body.appendChild(a);
    a.click();
    URL.revokeObjectURL(a.href);
    a.remove();
};

export default downloadFile;