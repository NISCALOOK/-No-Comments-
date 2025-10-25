
document.getElementById('btn').addEventListener('click', () => {
  fetch('/hola')
    .then(res => {
      if (!res.ok) throw new Error('Respuesta no OK: ' + res.status);
      return res.json();
    })
    .then(data => {
      document.getElementById('resultado').innerText = 
        `ID: ${data.id} - Texto: ${data.texto}`;
    })
    .catch(err => {
      document.getElementById('resultado').innerText = 
        'Error al obtener mensaje: ' + err.message;
    });
});