const approved = document.getElementById("approvedCount").value;
const pending = document.getElementById("pendingCount").value;
const rejected = document.getElementById("rejectedCount").value;

new Chart(document.getElementById("studentChart"),
    {
        type: 'pie',
        data: {
            labels: ['Approved', 'Pending', 'Rejected'],
            datasets: [{
                data: [approved, pending, rejected]
            }]
        }
    }
);